import * as React from 'react';
/** Tip / Warning / Note callout. Warnings are for This PC Only, Windows Firewall and the Sync Token. */
export interface CalloutProps {
  /** @default 'note' */
  kind?: 'tip' | 'warning' | 'note';
  title?: string;
  children?: React.ReactNode;
  style?: React.CSSProperties;
}
export declare function Callout(props: CalloutProps): JSX.Element;
