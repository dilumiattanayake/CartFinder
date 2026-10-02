/**
 * API base URL and route constants.
 * Import BASE_URL and ENDPOINTS throughout the app
 * to avoid hard-coded strings in service files.
 */
export const BASE_URL = process.env.EXPO_PUBLIC_API_BASE_URL || 'http://10.0.2.2:5000/api/v1';

export const ENDPOINTS = {
  // Auth
  REGISTER: '/auth/register',
  ME:       '/auth/me',

  // Vendors
  VENDORS_NEARBY:    '/vendors/nearby',
  VENDOR_BY_ID:      (id) => `/vendors/${id}`,
  VENDOR_BROADCAST:  (id) => `/vendors/${id}/broadcast`,
  VENDOR_STATUS:     (id) => `/vendors/${id}/status`,

  // Menus
  MENU_BY_VENDOR:    (vendorId) => `/menus/${vendorId}`,
  MENU_ADD_DISH:     (vendorId) => `/menus/${vendorId}/dishes`,
  MENU_DISH:         (vendorId, dishId) => `/menus/${vendorId}/dishes/${dishId}`,

  // Reviews
  REVIEWS_BY_VENDOR: (vendorId) => `/reviews/${vendorId}`,
  REVIEW_CREATE:     '/reviews',

  // Complaints
  COMPLAINTS:        '/complaints',
  COMPLAINT_RESOLVE: (id) => `/complaints/${id}/resolve`,

  // Inspections
  BADGE_BY_VENDOR:   (vendorId) => `/inspections/badge/${vendorId}`,
  BADGE_ISSUE:       '/inspections/badge',
  AUDIT_LOG:         '/inspections/audit',
  AUDIT_HISTORY:     (vendorId) => `/inspections/audit/${vendorId}`,
  VERIFY_QR:         '/inspections/verify-qr',
};
