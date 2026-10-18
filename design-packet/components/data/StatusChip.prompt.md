State chip; green fill only when something is ready/complete, otherwise a dark well with coloured text.
```jsx
<StatusChip tone="ready">READY</StatusChip>
<StatusChip tone="done">DONE</StatusChip>
<StatusChip tone="ready" onClick={goTimers}>Farming: 3</StatusChip>
```
- Labels are UPPERCASE for states, Title case for counts ("Birdhouses: 4").
