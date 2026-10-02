import React, { useEffect } from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { useAuth } from '../../hooks/useAuth';
import AppButton from '../../components/common/AppButton';
import { COLORS } from '../../constants/colors';
import { FONT_SIZES } from '../../constants/typography';

const SplashScreen = ({ navigation }) => {
  const { isSignedIn, role } = useAuth();

  useEffect(() => {
    const timer = setTimeout(() => {
      // If already signed in with a role, the RootNavigator will handle navigation
      // Otherwise, show RoleSelectScreen
      if (!isSignedIn) {
        navigation.replace('RoleSelect');
      }
    }, 1800);
    return () => clearTimeout(timer);
  }, [navigation, isSignedIn]);

  return (
    <View style={styles.container}>
      <Text style={styles.logo}>🛒</Text>
      <Text style={styles.title}>CartFinder</Text>
      <Text style={styles.subtitle}>Find your favourite food cart</Text>
      <AppButton
        title="Continue"
        onPress={() => navigation.replace('RoleSelect')}
        style={styles.button}
      />
    </View>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, justifyContent: 'center', alignItems: 'center', backgroundColor: COLORS.primary },
  logo:      { fontSize: 80, marginBottom: 16 },
  title:     { fontSize: FONT_SIZES.hero, fontWeight: '800', color: '#fff' },
  subtitle:  { fontSize: FONT_SIZES.base, color: '#FFE0D0', marginTop: 8 },
  button:    { marginTop: 32, minWidth: 180 },
});

export default SplashScreen;
