import * as React from 'react';
export interface SegmentedTab { id: string; label: string; /** Ready count; >0 shows "(n)" in green. */ count?: number; }
export interface SegmentedTabsProps {
  tabs: SegmentedTab[];
  value: string;
  onChange?: (id: string) => void;
  style?: React.CSSProperties;
}
export declare function SegmentedTabs(props: SegmentedTabsProps): JSX.Element;
