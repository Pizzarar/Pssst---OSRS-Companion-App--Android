import * as React from 'react';
export interface CodeBlockProps {
  /** Pass a string to enable copy. */
  children?: React.ReactNode;
  /** Small caption, e.g. "Command Prompt" or "App → Server URL". */
  label?: string;
  /** @default true */
  copyable?: boolean;
  style?: React.CSSProperties;
}
export declare function CodeBlock(props: CodeBlockProps): JSX.Element;
