import * as React from 'react';
/** The Android app's tab glyphs: menu, shield (Overview), chest (Storage), coins (Exchange), star (Progress), hourglass (Timers). */
export interface AppIconProps {
  /** @default 'shield' */
  name?: 'menu' | 'shield' | 'chest' | 'coins' | 'star' | 'hourglass';
  /** @default 24 */
  size?: number;
  /** Defaults to currentColor. */
  color?: string;
  style?: React.CSSProperties;
}
export declare function AppIcon(props: AppIconProps): JSX.Element;
