import * as React from 'react';
/** Line icon for docs (PC, phone, Wi-Fi, lock, cloud, clock, bell…). Lucide names: monitor, smartphone, wifi, lock, cloud, clock, bell, key-round, triangle-alert, lightbulb, info. */
export interface DocIconProps {
  /** Lucide icon name. @default 'monitor' */
  name?: string;
  /** @default 20 */
  size?: number;
  /** @default 'var(--text-muted)' */
  color?: string;
  style?: React.CSSProperties;
}
export declare function DocIcon(props: DocIconProps): JSX.Element;
