import * as React from 'react';
export interface SwitchProps extends Omit<React.ButtonHTMLAttributes<HTMLButtonElement>, 'onChange'> {
  checked?: boolean;
  /** @default false */
  defaultChecked?: boolean;
  onChange?: (checked: boolean) => void;
}
export declare function Switch(props: SwitchProps): JSX.Element;
