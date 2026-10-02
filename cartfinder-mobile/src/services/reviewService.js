import apiClient from './apiClient';
import { ENDPOINTS } from '../constants/api';

export const getReviews = (vendorId) =>
  apiClient.get(ENDPOINTS.REVIEWS_BY_VENDOR(vendorId));

export const submitReview = (reviewData) =>
  apiClient.post(ENDPOINTS.REVIEW_CREATE, reviewData);

export const fileComplaint = (complaintData) =>
  apiClient.post(ENDPOINTS.COMPLAINTS, complaintData);

export const getComplaints = (filters = {}) =>
  apiClient.get(ENDPOINTS.COMPLAINTS, { params: filters });

export const resolveComplaint = (id, data) =>
  apiClient.patch(ENDPOINTS.COMPLAINT_RESOLVE(id), data);
