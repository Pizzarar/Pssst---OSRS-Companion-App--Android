import * as React from 'react';
/** Status chip used on timer, GE and payment cards and the Activity Status row. */
export interface StatusChipProps {
  /** ready = green fill/black text (READY, GROWN, BOUGHT, SOLD, counts > 0); done = muted; neutral = BUILT; gold = BUYING; active = ACTIVE/SELLING/slayer count (yellow); danger = CANCELLED/Short. @default 'done' */
  tone?: 'ready' | 'done' | 'neutral' | 'gold' | 'active' | 'danger';
  onClick?: () => void;
  children?: React.ReactNode;
  style?: React.CSSProperties;
}
export declare function StatusChip(props: StatusChipProps): JSX.Element;
