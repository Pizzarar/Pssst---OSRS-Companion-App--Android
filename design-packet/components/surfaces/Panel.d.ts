import * as React from 'react';
/**
 * Raised stone panel. Every content block sits in one.
 * @startingPoint section="Surfaces" subtitle="Bevelled stone panel" viewport="700x260"
 */
export interface PanelProps extends React.HTMLAttributes<HTMLElement> {
  /** Inner padding in px. App uses 14 (headers), 12 (list cards), 10 (tight rows), 4 (tab bars). @default 14 */
  padding?: number | string;
  /** Corner radius in px. @default 6 */
  radius?: number;
  /** Element to render. @default 'div' */
  as?: keyof JSX.IntrinsicElements;
  children?: React.ReactNode;
}
export declare function Panel(props: PanelProps): JSX.Element;
