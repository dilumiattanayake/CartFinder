import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import { getMenu } from '../../services/menuService';

export const fetchMenu = createAsyncThunk(
  'menu/fetchMenu',
  async (vendorId, { rejectWithValue }) => {
    try {
      const response = await getMenu(vendorId);
      return response.data;
    } catch (err) {
      return rejectWithValue(err.message);
    }
  },
);

const menuSlice = createSlice({
  name: 'menu',
  initialState: {
    currentMenu: null,
    isLoading:   false,
    error:       null,
  },
  reducers: {
    clearMenu: (state) => { state.currentMenu = null; },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchMenu.pending,   (state) => { state.isLoading = true; state.error = null; })
      .addCase(fetchMenu.fulfilled,  (state, { payload }) => {
        state.isLoading   = false;
        state.currentMenu = payload;
      })
      .addCase(fetchMenu.rejected,   (state, { payload }) => {
        state.isLoading = false;
        state.error     = payload;
      });
  },
});

export const { clearMenu } = menuSlice.actions;
export default menuSlice.reducer;
