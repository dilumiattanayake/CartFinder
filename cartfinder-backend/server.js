'use strict';
require('dotenv').config();

const express = require('express');
const cors = require('cors');
const helmet = require('helmet');
const morgan = require('morgan');

const { connectDB } = require('./src/config/db');
const logger = require('./src/utils/logger');
require('./src/config/firebase'); // initialise Firebase Admin SDK on startup

const routes = require('./src/routes');
const errorMiddleware = require('./src/middlewares/error');

const app = express();
const PORT = process.env.PORT || 5000;

// ── Security & Parsing ────────────────────────────────────────────────────
app.use(helmet());
app.use(
  cors({
    origin: (origin, callback) => {
      // In development or when no origin header is present (mobile apps), allow
      if (process.env.NODE_ENV !== 'production' || !origin) {
        return callback(null, true);
      }
      const allowed = process.env.ALLOWED_ORIGINS ? process.env.ALLOWED_ORIGINS.split(',') : ['*'];
      if (allowed.includes('*') || allowed.includes(origin)) {
        return callback(null, true);
      }
      return callback(null, false);
    },
    credentials: true,
  }),
);
app.use(morgan('combined', { stream: { write: (msg) => logger.info(msg.trim()) } }));
app.use(express.json({ limit: '10mb' }));
app.use(express.urlencoded({ extended: true }));

// ── Health-check ──────────────────────────────────────────────────────────
app.get('/health', (_req, res) => res.json({ status: 'OK', ts: new Date().toISOString() }));

// ── API Routes ────────────────────────────────────────────────────────────
app.use('/api/v1', routes);

// ── Global Error Handler (must be last middleware) ────────────────────────
app.use(errorMiddleware);

// ── Bootstrap ─────────────────────────────────────────────────────────────
connectDB().then(() => {
  app.listen(PORT, () => {
    logger.info(`CartFinder API running on port ${PORT} [${process.env.NODE_ENV}]`);
  });
});

module.exports = app; // exported for testing
