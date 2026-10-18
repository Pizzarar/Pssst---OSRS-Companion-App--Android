import * as React from 'react';
export interface FlowNode { label: string; /** Lucide icon name (monitor, smartphone, wifi, cloud…). */ icon?: string; /** Mono caption, e.g. ":8788". */ sub?: string; }
/**
 * Connection path diagram (PC → phone, PC → Tailscale → phone, PC → Worker → phone).
 * @startingPoint section="Docs" subtitle="Connection path diagram" viewport="700x200"
 */
export interface ConnectionFlowProps {
  nodes: FlowNode[];
  style?: React.CSSProperties;
}
export declare function ConnectionFlow(props: ConnectionFlowProps): JSX.Element;
