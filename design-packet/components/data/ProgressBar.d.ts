import * as React from 'react';
export interface ProgressBarProps {
  /** 0–1. */
  value: number;
  /** success = growing patch / complete; accent = GE in progress / skill; danger = approval < 75%. @default 'success' */
  tone?: 'success' | 'accent' | 'danger';
  /** 4px instead of 6px (skill tiles). @default false */
  thin?: boolean;
  style?: React.CSSProperties;
}
export declare function ProgressBar(props: ProgressBarProps): JSX.Element;
