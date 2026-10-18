import * as React from 'react';
export interface FilterChipProps {
  /** @default false */
  selected?: boolean;
  /** square = bank sort modes; chip = quest filters. @default 'chip' */
  shape?: 'square' | 'chip';
  onClick?: () => void;
  children?: React.ReactNode;
  style?: React.CSSProperties;
}
export declare function FilterChip(props: FilterChipProps): JSX.Element;
