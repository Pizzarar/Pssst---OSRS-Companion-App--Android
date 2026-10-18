import * as React from 'react';
/** Recessed inset box for item slots, code blocks, input examples and screenshot frames. */
export interface SlotProps extends React.HTMLAttributes<HTMLDivElement> {
  /** false renders the darker Slot Empty fill. @default true */
  hasItem?: boolean;
  /** @default 3 */
  padding?: number | string;
  /** @default 4 */
  radius?: number;
  children?: React.ReactNode;
}
export declare function Slot(props: SlotProps): JSX.Element;
