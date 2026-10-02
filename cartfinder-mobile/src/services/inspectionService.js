import apiClient from './apiClient';
import { ENDPOINTS } from '../constants/api';

export const getHygieneBadge = (vendorId) =>
  apiClient.get(ENDPOINTS.BADGE_BY_VENDOR(vendorId));

export const issueBadge = (badgeData) =>
  apiClient.post(ENDPOINTS.BADGE_ISSUE, badgeData);

export const logAudit = (auditData) =>
  apiClient.post(ENDPOINTS.AUDIT_LOG, auditData);

export const getAuditHistory = (vendorId) =>
  apiClient.get(ENDPOINTS.AUDIT_HISTORY(vendorId));

export const verifyQRCode = (qrPayload) =>
  apiClient.post(ENDPOINTS.VERIFY_QR, { qrPayload });
