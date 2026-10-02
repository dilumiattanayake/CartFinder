import { getIdToken } from 'firebase/auth';
import { firebaseAuth } from '../config/firebase';

/**
 * Returns a helper function to get the current Firebase ID token.
 * The Firebase SDK handles auto-refresh when token is near expiry.
 *
 * Usage:
 *   const getToken = useFirebaseToken();
 *   const token = await getToken();
 */
export const useFirebaseToken = () => async () => {
  const user = firebaseAuth?.currentUser;
  if (!user) return null;
  return getIdToken(user, false);
};
