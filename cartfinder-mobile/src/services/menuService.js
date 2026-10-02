import apiClient from './apiClient';
import { ENDPOINTS } from '../constants/api';

export const getMenu = (vendorId) =>
  apiClient.get(ENDPOINTS.MENU_BY_VENDOR(vendorId));

export const saveMenu = (vendorId, menuData) =>
  apiClient.post(ENDPOINTS.MENU_BY_VENDOR(vendorId), menuData);

export const addDish = (vendorId, dishData) =>
  apiClient.post(ENDPOINTS.MENU_ADD_DISH(vendorId), dishData);

export const updateDish = (vendorId, dishId, dishData) =>
  apiClient.patch(ENDPOINTS.MENU_DISH(vendorId, dishId), dishData);

export const deleteDish = (vendorId, dishId) =>
  apiClient.delete(ENDPOINTS.MENU_DISH(vendorId, dishId));
