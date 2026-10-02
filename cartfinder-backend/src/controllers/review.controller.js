'use strict';
const asyncHandler = require('../utils/asyncHandler');
const { successResponse, ApiError } = require('../utils/apiResponse');
const Review = require('../models/Review');
const Vendor = require('../models/Vendor');
const schemas = require('../utils/validators');

exports.getByVendor = asyncHandler(async (req, res) => {
  const reviews = await Review.find({ vendorId: req.params.vendorId, isHidden: false })
    .sort({ createdAt: -1 })
    .limit(50);
  return res.json(successResponse(reviews));
});

exports.create = asyncHandler(async (req, res, next) => {
  const { error, value } = schemas.submitReview.validate(req.body);
  if (error) return next(new ApiError(422, error.message));

  const reviewerUid = req.user.firebaseUser.uid;
  const review = await Review.create({ ...value, reviewerUid });

  // Recalculate vendor average rating
  const stats = await Review.aggregate([
    { $match: { vendorId: review.vendorId, isHidden: false } },
    { $group: { _id: null, avg: { $avg: '$rating' }, count: { $sum: 1 } } },
  ]);
  if (stats.length) {
    await Vendor.findByIdAndUpdate(review.vendorId, {
      averageRating: Math.round(stats[0].avg * 10) / 10,
      reviewCount:   stats[0].count,
    });
  }

  return res.status(201).json(successResponse(review, 'Review submitted'));
});

exports.remove = asyncHandler(async (req, res, next) => {
  const review = await Review.findByIdAndDelete(req.params.reviewId);
  if (!review) return next(new ApiError(404, 'Review not found'));
  return res.json(successResponse(null, 'Review removed'));
});
