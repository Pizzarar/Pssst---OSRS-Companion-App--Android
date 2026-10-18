import * as React from 'react';
export interface StepNumberProps {
  n: number | string;
  /** @default 28 */
  size?: number;
  style?: React.CSSProperties;
}
export declare function StepNumber(props: StepNumberProps): JSX.Element;
