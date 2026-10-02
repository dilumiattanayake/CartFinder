'use strict';
const mongoose = require('mongoose');
const logger = require('../utils/logger');

/**
 * Establishes a Mongoose connection to MongoDB Atlas.
 * Uses connection pooling defaults. Retries are handled by
 * the MongoDB driver (serverSelectionTimeoutMS).
 */
const connectDB = async () => {
  try {
    const conn = await mongoose.connect(process.env.MONGODB_URI, {
      serverSelectionTimeoutMS: 5000,
    });
    logger.info(`MongoDB connected: ${conn.connection.host}`);
  } catch (err) {
    logger.error(`MongoDB connection error: ${err.message}`);
    process.exit(1);
  }
};

module.exports = { connectDB };
