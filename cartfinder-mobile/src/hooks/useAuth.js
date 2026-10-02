import { useSelector } from 'react-redux';
import { ROLES } from '../constants/roles';

/**
 * Returns the current authenticated user state from Redux.
 */
export const useAuth = () => {
  const { user, role, isSignedIn, isLoading, error } = useSelector((s) => s.auth);
  return {
    user,
    role,
    isSignedIn,
    isLoading,
    error,
    isCustomer: role === ROLES.CUSTOMER,
    isVendor:   role === ROLES.VENDOR,
    isPhi:      role === ROLES.PHI,
    isAdmin:    role === ROLES.ADMIN,
  };
};
