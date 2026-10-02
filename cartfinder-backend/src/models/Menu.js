'use strict';
const mongoose = require('mongoose');

/** Individual dish / item sub-document */
const DishSchema = new mongoose.Schema(
  {
    name:        { type: String, required: true, trim: true },
    description: { type: String, maxlength: 300 },
    price:       { type: Number, required: true, min: 0 },
    currency:    { type: String, default: 'LKR' },
    imageUrl:    { type: String },
    isAvailable: { type: Boolean, default: true },
    isSpecial:   { type: Boolean, default: false }, // daily special flag
    category:    { type: String },                   // e.g. 'Rice', 'Drinks'
    allergens:   [{ type: String }],
  },
  { timestamps: true },
);

/** Menu document linked to a Vendor */
const MenuSchema = new mongoose.Schema(
  {
    vendorId: {
      type:     mongoose.Schema.Types.ObjectId,
      ref:      'Vendor',
      required: true,
      unique:   true,
    },
    dishes: [DishSchema],
    lastUpdated: { type: Date, default: Date.now },
  },
  { timestamps: true },
);

module.exports = mongoose.model('Menu', MenuSchema);
