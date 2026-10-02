import axios from 'axios';
import { BASE_URL } from '../constants/api';
import { firebaseAuth } from '../config/firebase';

/**
 * Axios instance pre-configured for CartFinder API.
 *
 * Token Interceptor:
 *  - Before each request, retrieves the current Firebase user's ID token.
 *  - Attaches it as a Bearer token in the Authorization header.
 *  - Firebase SDK automatically refreshes tokens nearing expiry (< 5 min).
 *
 * Response Interceptor:
 *  - Unwraps the { success, data, message } envelope on success.
 *  - Normalises errors into a consistent { status, message, errors } shape.
 */
const apiClient = axios.create({
  baseURL: BASE_URL,
  timeout: 15000,
  headers: { 'Content-Type': 'application/json' },
});

// ── Request Interceptor: Attach Firebase ID Token ─────────────────────────
apiClient.interceptors.request.use(
  async (config) => {
    const currentUser = firebaseAuth?.currentUser;
    if (currentUser) {
      // forceRefresh=false: Firebase handles automatic refresh when < 5 min to expiry
      const token = await currentUser.getIdToken(false);
      config.headers.Authorization = `Bearer ${token}`;
      console.log(`[apiClient] ${config.method.toUpperCase()} ${config.url}`, {
        hasToken: !!token,
        tokenPreview: token ? `${token.substring(0, 20)}...` : 'none',
      });
    } else {
      console.warn('[apiClient] No Firebase user for request:', config.url);
    }
    return config;
  },
  (error) => Promise.reject(error),
);

// ── Response Interceptor: Unwrap envelope + normalise errors ──────────────
apiClient.interceptors.response.use(
  (response) => response.data,  // returns { success, data, message } directly
  async (error) => {
    const { response } = error;

    if (response?.status === 401) {
      // Token is truly invalid (not just expired — Firebase handles expiry).
      // Consider dispatching a store signOut action here:
      // store.dispatch(signOut());
      console.warn('[apiClient] 401 Unauthorized — user may need to re-authenticate');
    }

    return Promise.reject({
      status:  response?.status  ?? 0,
      message: response?.data?.message ?? 'Network error. Please try again.',
      errors:  response?.data?.errors  ?? [],
    });
  },
);

export default apiClient;
