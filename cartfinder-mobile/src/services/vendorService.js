import apiClient from './apiClient';
import { ENDPOINTS } from '../constants/api';

/** Fetch vendors near a GPS coordinate. */
export const getNearbyVendors = ({ lat, lng, radius }) =>
  apiClient.get(ENDPOINTS.VENDORS_NEARBY, { params: { lat, lng, radius } });

/** Fetch a single vendor's full profile. */
export const getVendorById = (id) =>
  apiClient.get(ENDPOINTS.VENDOR_BY_ID(id));

/** Create a new vendor profile (vendor role required). */
export const createVendorProfile = (data) =>
  apiClient.post('/vendors', data);

/** Update a vendor's profile. */
export const updateVendorProfile = (id, data) =>
  apiClient.put(ENDPOINTS.VENDOR_BY_ID(id), data);

/**
 * Toggle live location broadcast.
 * @param {string} id - Vendor MongoDB ID
 * @param {{ latitude, longitude, label }} locationData
 */
export const broadcastLocation = (id, locationData) =>
  apiClient.patch(ENDPOINTS.VENDOR_BROADCAST(id), locationData);

/** Stop broadcasting (set isLive: false). */
export const stopBroadcast = (id) =>
  apiClient.patch(ENDPOINTS.VENDOR_BY_ID(id), { isLive: false });
