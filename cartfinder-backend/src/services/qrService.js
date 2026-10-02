'use strict';

const crypto = require('crypto');

const QR_VERSION = 1;
const QR_TYPE = 'hygiene_badge';
const QR_TTL_MS = 30 * 24 * 60 * 60 * 1000;

const getSecret = () => {
  const secret = process.env.QR_SECRET;
  if (!secret || secret === 'changeme') {
    throw new Error('QR_SECRET must be configured with a strong secret');
  }
  return secret;
};

const sign = (encodedPayload) => crypto
  .createHmac('sha256', getSecret())
  .update(encodedPayload)
  .digest('base64url');

const encode = (payload) => Buffer
  .from(JSON.stringify(payload), 'utf8')
  .toString('base64url');

const decode = (encodedPayload) => JSON.parse(
  Buffer.from(encodedPayload, 'base64url').toString('utf8'),
);

const generateBadgeQR = async ({ vendorId, grade, phiUid }) => {
  const now = Date.now();
  const payload = {
    version: QR_VERSION,
    type: QR_TYPE,
    vendorId: String(vendorId),
    grade,
    phiUid,
    issuedAt: new Date(now).toISOString(),
    expiresAt: new Date(now + QR_TTL_MS).toISOString(),
  };
  const encodedPayload = encode(payload);
  return `${encodedPayload}.${sign(encodedPayload)}`;
};

const verifyBadgeQR = async (qrPayload) => {
  if (typeof qrPayload !== 'string') return { valid: false };

  const [encodedPayload, providedSignature, ...extraParts] = qrPayload.split('.');
  if (!encodedPayload || !providedSignature || extraParts.length > 0) {
    return { valid: false };
  }

  try {
    const expectedSignature = sign(encodedPayload);
    const providedBuffer = Buffer.from(providedSignature, 'utf8');
    const expectedBuffer = Buffer.from(expectedSignature, 'utf8');

    if (
      providedBuffer.length !== expectedBuffer.length
      || !crypto.timingSafeEqual(providedBuffer, expectedBuffer)
    ) {
      return { valid: false };
    }

    const data = decode(encodedPayload);
    const expiresAt = Date.parse(data.expiresAt);
    const isValid = data.version === QR_VERSION
      && data.type === QR_TYPE
      && typeof data.vendorId === 'string'
      && ['A', 'B', 'C', 'D'].includes(data.grade)
      && typeof data.phiUid === 'string'
      && Number.isFinite(expiresAt)
      && expiresAt > Date.now();

    return isValid ? { valid: true, data } : { valid: false };
  } catch (_error) {
    return { valid: false };
  }
};

module.exports = { generateBadgeQR, verifyBadgeQR };
