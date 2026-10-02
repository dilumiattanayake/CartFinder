import {
  createUserWithEmailAndPassword,
  sendPasswordResetEmail,
  signInWithEmailAndPassword,
  signOut as firebaseSignOut,
} from 'firebase/auth';
import apiClient from './apiClient';
import { ENDPOINTS } from '../constants/api';
import { firebaseAuth } from '../config/firebase';

const requireAuth = () => {
  if (!firebaseAuth) throw new Error('Firebase is not configured for this app');
  return firebaseAuth;
};

/**
 * Firebase Client Auth operations.
 * All methods return the Firebase UserCredential or throw on failure.
 */

/** Sign in with email and password. */
export const signInWithEmail = (email, password) =>
  signInWithEmailAndPassword(requireAuth(), email, password);

/** Create a new Firebase user account. */
export const registerWithEmail = (email, password) =>
  createUserWithEmailAndPassword(requireAuth(), email, password);

/** Sign out the current Firebase session. */
export const signOut = () => firebaseSignOut(requireAuth());

/** Send a password-reset email. */
export const sendPasswordReset = (email) => sendPasswordResetEmail(requireAuth(), email);

/**
 * Register the signed-in Firebase user in the CartFinder MongoDB backend.
 * Must be called AFTER Firebase sign-up, with the user already authenticated.
 *
 * @param {string} role - 'customer' | 'vendor' | 'phi'
 */
export const registerInBackend = async (role) => {
  const currentUser = requireAuth().currentUser;
  if (!currentUser) throw new Error('No authenticated Firebase user');

  const { uid, email, displayName } = currentUser;
  console.log('[authService] Registering backend with:', { firebaseUid: uid, email, role, displayName });
  
  try {
    const response = await apiClient.post(ENDPOINTS.REGISTER, { 
      firebaseUid: uid, 
      email, 
      role, 
      displayName: displayName || email.split('@')[0]  // Use email prefix as fallback
    });
    console.log('[authService] Backend registration successful:', response);
    return response;
  } catch (err) {
    console.error('[authService] Backend registration failed:', err);
    throw err;
  }
};

/** Fetch the current user's MongoDB profile. */
export const fetchMyProfile = () => apiClient.get(ENDPOINTS.ME);
