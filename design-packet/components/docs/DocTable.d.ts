import * as React from 'react';
export interface DocTableProps {
  columns: React.ReactNode[];
  rows: React.ReactNode[][];
  style?: React.CSSProperties;
}
export declare function DocTable(props: DocTableProps): JSX.Element;
