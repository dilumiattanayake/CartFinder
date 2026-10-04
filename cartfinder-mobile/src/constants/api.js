import { Platform, NativeModules } from 'react-native';
import Constants from 'expo-constants';

const resolveBaseUrl = () => {
  const envUrl = process.env.EXPO_PUBLIC_API_BASE_URL;

  // If envUrl is explicitly set to a custom URL (not emulator loopback on device)
  if (envUrl && !envUrl.includes('10.0.2.2') && !envUrl.includes('localhost')) {
    return envUrl;
  }

  // Detect Metro dev server host from Expo Constants or NativeModules
  try {
    const hostUri = Constants?.expoConfig?.hostUri || Constants?.manifest2?.extra?.expoGo?.debuggerHost;
    if (hostUri) {
      const host = hostUri.split(':')[0];
      if (host && host !== 'localhost' && host !== '127.0.0.1') {
        return `http://${host}:5001/api/v1`;
      }
    }

    const scriptURL = NativeModules?.SourceCode?.scriptURL;
    if (scriptURL) {
      const host = scriptURL.split('://')[1]?.split('/')[0]?.split(':')[0];
      if (host && host !== 'localhost' && host !== '127.0.0.1') {
        return `http://${host}:5001/api/v1`;
      }
    }
  } catch (_e) {
    // fallback
  }

  if (envUrl) return envUrl;
  return Platform.OS === 'android' ? 'http://172.20.212.60:5001/api/v1' : 'http://localhost:5001/api/v1';
};

export const BASE_URL = resolveBaseUrl();

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
