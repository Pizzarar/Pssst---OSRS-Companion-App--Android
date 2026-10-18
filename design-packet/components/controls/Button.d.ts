import * as React from 'react';
/**
 * Primary action. Filled = gold pill with black label; text = coloured label only.
 * @startingPoint section="Controls" subtitle="Gold filled and text buttons" viewport="700x200"
 */
export interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  /** @default 'filled' */
  variant?: 'filled' | 'text';
  /** accent = OSRS Gold; danger = Red (Disconnect); sync = stack yellow, used by the app's "Sync" text button only. @default 'accent' */
  tone?: 'accent' | 'danger' | 'sync';
  /** @default false */
  fullWidth?: boolean;
  children?: React.ReactNode;
}
export declare function Button(props: ButtonProps): JSX.Element;
