Equal-width sub-tabs in a 4px-padded panel; selected tab sits in a Slot Empty well with gold bold text.
```jsx
<SegmentedTabs value="dailies" onChange={setTab} tabs={[{id:'dailies',label:'Dailies',count:2},{id:'birdhouses',label:'Birdhouses'}]} />
```
- `count > 0` on an unselected tab colours it Herb Green.
