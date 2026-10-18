import * as React from 'react';
export interface CheckboxProps {
  checked?: boolean;
  /** @default false */
  defaultChecked?: boolean;
  onChange?: (checked: boolean) => void;
  label?: React.ReactNode;
  style?: React.CSSProperties;
}
export declare function Checkbox(props: CheckboxProps): JSX.Element;
