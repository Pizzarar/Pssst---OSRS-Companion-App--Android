import * as React from 'react';
/** Square item slot for the bank grid (72px min) and inventory (4 × 7). */
export interface ItemSlotProps {
  name?: string;
  /** Stack size. <100K yellow, 100K–9.99M white, ≥10M green (game convention). Hidden when ≤1. */
  quantity?: number;
  /** Pixel-art item icon (rendered pixelated). Without it, the name is shown centred. */
  iconSrc?: string;
  /** Empty inventory square. @default false */
  empty?: boolean;
  /** Shown faintly in empty inventory slots. */
  slotNumber?: number;
  /** Fixed width; omit to fill the grid cell. */
  size?: number;
  style?: React.CSSProperties;
}
export declare function ItemSlot(props: ItemSlotProps): JSX.Element;
export declare function formatQuantity(q: number): string;
