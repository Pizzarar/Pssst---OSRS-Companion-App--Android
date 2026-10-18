import * as React from 'react';
/** Outlined input used for Server URL, Sync Token and search fields. */
export interface TextFieldProps extends Omit<React.InputHTMLAttributes<HTMLInputElement>, 'style'> {
  label?: string;
  placeholder?: string;
  /** Red helper line under the field (e.g. the 127.0.0.1 warning). */
  supportingText?: React.ReactNode;
  /** @default false */
  error?: boolean;
  /** Use JetBrains Mono for the value (URLs, tokens). @default false */
  mono?: boolean;
  style?: React.CSSProperties;
  inputStyle?: React.CSSProperties;
}
export declare function TextField(props: TextFieldProps): JSX.Element;
