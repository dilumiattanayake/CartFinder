/** Application role constants. Must match backend role enum exactly. */
export const ROLES = {
  CUSTOMER: 'customer',
  VENDOR:   'vendor',
  PHI:      'phi',
  ADMIN:    'admin',
};

export const ROLE_LABELS = {
  [ROLES.CUSTOMER]: 'Customer / Student',
  [ROLES.VENDOR]:   'Vendor / Stall Owner',
  [ROLES.PHI]:      'Public Health Inspector',
  [ROLES.ADMIN]:    'Administrator',
};
