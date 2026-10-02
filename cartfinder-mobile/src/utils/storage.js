import AsyncStorage from '@react-native-async-storage/async-storage';

/** Persist a JSON-serialisable value under a key. */
export const storeItem = async (key, value) => {
  await AsyncStorage.setItem(key, JSON.stringify(value));
};

/** Retrieve and parse a stored value. Returns null if not found. */
export const getItem = async (key) => {
  const raw = await AsyncStorage.getItem(key);
  return raw ? JSON.parse(raw) : null;
};

/** Remove a stored item. */
export const removeItem = (key) => AsyncStorage.removeItem(key);

/** Remove all stored items (use carefully). */
export const clearAll = () => AsyncStorage.clear();
