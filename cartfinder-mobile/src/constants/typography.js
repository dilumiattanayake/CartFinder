import { Platform } from 'react-native';

export const FONTS = {
  regular:  Platform.OS === 'ios' ? 'System' : 'Roboto',
  medium:   Platform.OS === 'ios' ? 'System' : 'Roboto-Medium',
  bold:     Platform.OS === 'ios' ? 'System' : 'Roboto-Bold',
};

export const FONT_SIZES = {
  xs:   11,
  sm:   13,
  base: 15,
  md:   17,
  lg:   20,
  xl:   24,
  xxl:  30,
  hero: 38,
};

export const LINE_HEIGHTS = {
  tight:   1.2,
  normal:  1.5,
  relaxed: 1.75,
};
