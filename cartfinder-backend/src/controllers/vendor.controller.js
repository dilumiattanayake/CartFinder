'use strict';
const asyncHandler = require('../utils/asyncHandler');
const { successResponse, ApiError } = require('../utils/apiResponse');
const Vendor = require('../models/Vendor');
const schemas = require('../utils/validators');

/** GET /api/v1/vendors/nearby?lat=&lng=&radius= */
exports.getNearby = asyncHandler(async (req, res, next) => {
  const { error, value } = schemas.nearbyVendors.validate(req.query);
  if (error) return next(new ApiError(400, error.message));

  const { lat, lng, radius } = value;

  const vendors = await Vendor.find({
    isActive: true,
    approvalStatus: 'approved',
    location: {
      $near: {
        $geometry:    { type: 'Point', coordinates: [lng, lat] },
        $maxDistance: radius,
      },
    },
  }).select('-__v');

  return res.json(successResponse(vendors, `Found ${vendors.length} vendor(s) nearby`));
});

/** GET /api/v1/vendors/:id */
exports.getById = asyncHandler(async (req, res, next) => {
  const vendor = await Vendor.findById(req.params.id);
  if (!vendor) return next(new ApiError(404, 'Vendor not found'));
  return res.json(successResponse(vendor));
});

/** POST /api/v1/vendors */
exports.create = asyncHandler(async (req, res, next) => {
  const ownerUid = req.user.firebaseUser.uid;
  const existing = await Vendor.findOne({ ownerUid });
  if (existing) return next(new ApiError(409, 'Vendor profile already exists'));

  const vendor = await Vendor.create({ ...req.body, ownerUid });
  return res.status(201).json(successResponse(vendor, 'Vendor profile created'));
});

/** PUT /api/v1/vendors/:id */
exports.update = asyncHandler(async (req, res, next) => {
  const vendor = await Vendor.findByIdAndUpdate(
    req.params.id, req.body, { new: true, runValidators: true },
  );
  if (!vendor) return next(new ApiError(404, 'Vendor not found'));
  return res.json(successResponse(vendor, 'Vendor updated'));
});

/** PATCH /api/v1/vendors/:id/broadcast */
exports.toggleBroadcast = asyncHandler(async (req, res, next) => {
  const { error, value } = schemas.updateVendorLocation.validate(req.body);
  if (error) return next(new ApiError(400, error.message));

  const { latitude, longitude, label } = value;
  const vendor = await Vendor.findOneAndUpdate(
    { _id: req.params.id, ownerUid: req.user.firebaseUser.uid },
    {
      isLive: true,
      lastBroadcast: new Date(),
      locationLabel: label,
      location: { type: 'Point', coordinates: [longitude, latitude] },
    },
    { new: true, runValidators: true },
  );
  if (!vendor) return next(new ApiError(404, 'Vendor not found or unauthorised'));
  return res.json(successResponse(vendor, 'Location broadcast updated'));
});

/** PATCH /api/v1/vendors/:id/status */
exports.updateStatus = asyncHandler(async (req, res, next) => {
  const { approvalStatus } = req.body;
  const vendor = await Vendor.findByIdAndUpdate(
    req.params.id, { approvalStatus }, { new: true },
  );
  if (!vendor) return next(new ApiError(404, 'Vendor not found'));
  return res.json(successResponse(vendor, `Vendor status updated to ${approvalStatus}`));
});
