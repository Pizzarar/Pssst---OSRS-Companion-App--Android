import * as React from 'react';
export interface TopBarProps {
  /** Current tab title; rendered as "Pssst • {title}". @default 'Overview' */
  title?: string;
  /** Relative time, e.g. "2 min. ago". */
  syncedAgo?: string;
  onMenu?: () => void;
  onSync?: () => void;
  /** @default 'Sync' */
  actionLabel?: string;
  style?: React.CSSProperties;
}
export declare function TopBar(props: TopBarProps): JSX.Element;
