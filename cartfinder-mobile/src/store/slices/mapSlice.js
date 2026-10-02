import { createSlice } from '@reduxjs/toolkit';
import { DEFAULT_REGION } from '../../constants/mapConfig';

const mapSlice = createSlice({
  name: 'map',
  initialState: {
    region:        DEFAULT_REGION,
    userLocation:  null,            // { latitude, longitude }
    showLiveOnly:  false,
    filterCategory: null,           // 'Food' | 'Beverages' | etc.
  },
  reducers: {
    setRegion:         (state, { payload }) => { state.region       = payload; },
    setUserLocation:   (state, { payload }) => { state.userLocation  = payload; },
    toggleLiveFilter:  (state) =>               { state.showLiveOnly = !state.showLiveOnly; },
    setFilterCategory: (state, { payload }) => { state.filterCategory = payload; },
    resetFilters:      (state) => {
      state.showLiveOnly   = false;
      state.filterCategory = null;
    },
  },
});

export const {
  setRegion, setUserLocation, toggleLiveFilter, setFilterCategory, resetFilters,
} = mapSlice.actions;
export default mapSlice.reducer;
