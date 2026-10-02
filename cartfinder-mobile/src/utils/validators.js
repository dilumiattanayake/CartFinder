/** Simple form-field validation helpers */

export const isValidEmail = (email) =>
  /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.trim());

export const isStrongPassword = (password) =>
  password.length >= 8;

export const isValidPhone = (phone) =>
  /^\+?[\d\s\-()]{7,15}$/.test(phone);

export const isNotEmpty = (value) =>
  value !== null && value !== undefined && String(value).trim().length > 0;

/** Returns an error string or null for a review rating. */
export const validateRating = (rating) => {
  if (!rating) return 'Rating is required';
  if (rating < 1 || rating > 5) return 'Rating must be between 1 and 5';
  return null;
};
