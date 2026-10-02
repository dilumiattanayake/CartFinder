/**
 * Shared formatting helpers.
 */

/** Format a distance in metres to a human-readable string. */
export const formatDistance = (metres) => {
  if (metres < 1000) return `${Math.round(metres)} m`;
  return `${(metres / 1000).toFixed(1)} km`;
};

/** Format a price number to a locale currency string. */
export const formatPrice = (amount, currency = 'LKR') =>
  new Intl.NumberFormat('en-LK', { style: 'currency', currency }).format(amount);

/** Format an ISO date string to 'Jan 12, 2025'. */
export const formatDate = (isoString) =>
  new Date(isoString).toLocaleDateString('en-US', {
    year: 'numeric', month: 'short', day: 'numeric',
  });

/** Format a time string 'HH:MM' to '8:30 AM'. */
export const formatTime = (timeStr) => {
  const [h, m] = timeStr.split(':').map(Number);
  const ampm = h >= 12 ? 'PM' : 'AM';
  const hour = h % 12 || 12;
  return `${hour}:${String(m).padStart(2, '0')} ${ampm}`;
};

/** Truncate a string to maxLen characters with ellipsis. */
export const truncate = (str, maxLen = 100) =>
  str.length > maxLen ? `${str.slice(0, maxLen)}...` : str;
