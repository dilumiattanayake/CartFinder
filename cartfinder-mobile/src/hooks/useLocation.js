import { useState, useEffect } from 'react';
import { useDispatch } from 'react-redux';
import { setUserLocation } from '../store/slices/mapSlice';
import {
  requestLocationPermission,
  getCurrentPosition,
} from '../services/locationService';

/**
 * Requests location permission and fetches the device's current position.
 * Dispatches the result to the Redux map slice.
 */
export const useLocation = () => {
  const dispatch = useDispatch();
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    (async () => {
      try {
        const granted = await requestLocationPermission();
        if (!granted) {
          setError('Location permission denied');
          setLoading(false);
          return;
        }
        const coords = await getCurrentPosition();
        dispatch(setUserLocation(coords));
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    })();
  }, [dispatch]);

  return { error, loading };
};
