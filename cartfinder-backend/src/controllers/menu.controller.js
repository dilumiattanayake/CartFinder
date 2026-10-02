'use strict';
const asyncHandler = require('../utils/asyncHandler');
const { successResponse, ApiError } = require('../utils/apiResponse');
const Menu = require('../models/Menu');

exports.getByVendor = asyncHandler(async (req, res) => {
  const menu = await Menu.findOne({ vendorId: req.params.vendorId });
  return res.json(successResponse(menu));
});

exports.createOrUpdate = asyncHandler(async (req, res) => {
  const menu = await Menu.findOneAndUpdate(
    { vendorId: req.params.vendorId },
    { ...req.body, vendorId: req.params.vendorId, lastUpdated: Date.now() },
    { new: true, upsert: true, runValidators: true },
  );
  return res.json(successResponse(menu, 'Menu saved'));
});

exports.addDish = asyncHandler(async (req, res, next) => {
  const menu = await Menu.findOne({ vendorId: req.params.vendorId });
  if (!menu) return next(new ApiError(404, 'Menu not found'));
  menu.dishes.push(req.body);
  await menu.save();
  return res.status(201).json(successResponse(menu, 'Dish added'));
});

exports.updateDish = asyncHandler(async (req, res, next) => {
  const menu = await Menu.findOne({ vendorId: req.params.vendorId });
  if (!menu) return next(new ApiError(404, 'Menu not found'));
  const dish = menu.dishes.id(req.params.dishId);
  if (!dish) return next(new ApiError(404, 'Dish not found'));
  Object.assign(dish, req.body);
  await menu.save();
  return res.json(successResponse(menu, 'Dish updated'));
});

exports.deleteDish = asyncHandler(async (req, res, next) => {
  const menu = await Menu.findOne({ vendorId: req.params.vendorId });
  if (!menu) return next(new ApiError(404, 'Menu not found'));
  const dish = menu.dishes.id(req.params.dishId);
  if (!dish) return next(new ApiError(404, 'Dish not found'));
  dish.deleteOne();
  await menu.save();
  return res.json(successResponse(null, 'Dish deleted'));
});
