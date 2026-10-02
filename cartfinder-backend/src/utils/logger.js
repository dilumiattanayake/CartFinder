'use strict';
const { createLogger, format, transports } = require('winston');

/**
 * Application logger using Winston.
 * In development: colourised console output.
 * In production:  JSON-formatted console output (compatible with log aggregators).
 */
const isDev = process.env.NODE_ENV !== 'production';

const logger = createLogger({
  level: isDev ? 'debug' : 'info',
  format: isDev
    ? format.combine(
        format.colorize(),
        format.timestamp({ format: 'HH:mm:ss' }),
        format.printf(({ timestamp, level, message }) => `${timestamp} [${level}]: ${message}`),
      )
    : format.combine(format.timestamp(), format.json()),
  transports: [new transports.Console()],
});

module.exports = logger;
