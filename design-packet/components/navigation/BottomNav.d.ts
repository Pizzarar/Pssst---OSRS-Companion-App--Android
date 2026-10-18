import * as React from 'react';
export interface BottomNavItem { id: string; label: string; icon: 'shield' | 'chest' | 'coins' | 'star' | 'hourglass' | 'menu'; }
export interface BottomNavProps {
  /** Defaults to Overview / Storage / Exchange / Progress / Timers. */
  items?: BottomNavItem[];
  value: string;
  onChange?: (id: string) => void;
  style?: React.CSSProperties;
}
export declare function BottomNav(props: BottomNavProps): JSX.Element;
