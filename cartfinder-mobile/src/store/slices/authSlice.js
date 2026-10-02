import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import * as authService from '../../services/authService';

/** Async thunk: fetch MongoDB profile after Firebase auth state change */
export const loadUserProfile = createAsyncThunk(
  'auth/loadUserProfile',
  async (_, { rejectWithValue }) => {
    try {
      const response = await authService.fetchMyProfile();
      return response.data;
    } catch (err) {
      return rejectWithValue(err.message);
    }
  },
);

const authSlice = createSlice({
  name: 'auth',
  initialState: {
    user:        null,   // MongoDB user document
    role:        null,   // 'customer' | 'vendor' | 'phi' | 'admin'
    isLoading:   false,
    isSignedIn:  false,
    error:       null,
  },
  reducers: {
    setFirebaseUser: (state, { payload }) => {
      state.isSignedIn = !!payload;
    },
    setUser: (state, { payload }) => {
      state.user      = payload;
      state.role      = payload?.role ?? null;
      state.isSignedIn = !!payload;
    },
    clearAuth: (state) => {
      state.user      = null;
      state.role      = null;
      state.isSignedIn = false;
      state.error     = null;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(loadUserProfile.pending,  (state) => { state.isLoading = true; state.error = null; })
      .addCase(loadUserProfile.fulfilled, (state, { payload }) => {
        state.isLoading  = false;
        state.user       = payload;
        state.role       = payload?.role;
        state.isSignedIn = true;
      })
      .addCase(loadUserProfile.rejected,  (state, { payload }) => {
        state.isLoading = false;
        state.error     = payload;
      });
  },
});

export const { setFirebaseUser, setUser, clearAuth } = authSlice.actions;
export default authSlice.reducer;
