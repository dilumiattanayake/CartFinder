import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import { getNearbyVendors } from '../../services/vendorService';

export const fetchNearbyVendors = createAsyncThunk(
  'vendor/fetchNearby',
  async ({ lat, lng, radius }, { rejectWithValue }) => {
    try {
      const response = await getNearbyVendors({ lat, lng, radius });
      return response.data;
    } catch (err) {
      return rejectWithValue(err.message);
    }
  },
);

const vendorSlice = createSlice({
  name: 'vendor',
  initialState: {
    nearbyList:     [],
    selectedVendor: null,
    isLoading:      false,
    error:          null,
  },
  reducers: {
    selectVendor:   (state, { payload }) => { state.selectedVendor = payload; },
    clearSelection: (state) => { state.selectedVendor = null; },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchNearbyVendors.pending,   (state) => { state.isLoading = true; state.error = null; })
      .addCase(fetchNearbyVendors.fulfilled,  (state, { payload }) => {
        state.isLoading  = false;
        state.nearbyList = payload;
      })
      .addCase(fetchNearbyVendors.rejected,   (state, { payload }) => {
        state.isLoading = false;
        state.error     = payload;
      });
  },
});

export const { selectVendor, clearSelection } = vendorSlice.actions;
export default vendorSlice.reducer;
