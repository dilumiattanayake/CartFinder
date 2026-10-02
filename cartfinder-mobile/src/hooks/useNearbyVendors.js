import { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { fetchNearbyVendors } from '../store/slices/vendorSlice';
import { NEARBY_RADIUS_METRES } from '../constants/mapConfig';

/**
 * Fetches nearby vendors whenever the user's location is available in Redux.
 * Re-fetches on location change.
 */
export const useNearbyVendors = () => {
  const dispatch     = useDispatch();
  const userLocation = useSelector((s) => s.map.userLocation);
  const { nearbyList, isLoading, error } = useSelector((s) => s.vendor);

  useEffect(() => {
    if (userLocation) {
      dispatch(fetchNearbyVendors({
        lat:    userLocation.latitude,
        lng:    userLocation.longitude,
        radius: NEARBY_RADIUS_METRES,
      }));
    }
  }, [userLocation, dispatch]);

  return { vendors: nearbyList, isLoading, error };
};
