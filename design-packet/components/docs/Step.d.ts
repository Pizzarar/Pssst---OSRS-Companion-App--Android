import * as React from 'react';
export interface StepProps {
  n: number | string;
  /** The single action for this step. */
  children?: React.ReactNode;
  /** Optional content under the step (code block, screenshot slot, callout). */
  detail?: React.ReactNode;
  style?: React.CSSProperties;
}
export declare function Step(props: StepProps): JSX.Element;
