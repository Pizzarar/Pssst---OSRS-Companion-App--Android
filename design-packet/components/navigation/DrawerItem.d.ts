import * as React from 'react';
export interface DrawerItemProps {
  /** @default false */
  selected?: boolean;
  onClick?: () => void;
  children?: React.ReactNode;
  style?: React.CSSProperties;
}
export declare function DrawerItem(props: DrawerItemProps): JSX.Element;
