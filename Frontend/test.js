
function findMathTotal(amounts) {
    let sorted = [...new Set(amounts)].sort((a, b) => a - b);
    let possibleTotals = [];
    
    // Find all A + B = C
    for (let i = 0; i < sorted.length - 2; i++) {
        for (let j = i + 1; j < sorted.length - 1; j++) {
            for (let k = j + 1; k < sorted.length; k++) {
                let a = sorted[i];
                let b = sorted[j];
                let c = sorted[k];
                if (Math.abs((a + b) - c) < 1.0) {
                    // C could be Total (Subtotal + Tax = Total)
                    // Or C could be Cash (Total + Change = Cash)
                    // If C % 50000 == 0 or C % 10000 == 0, it is likely Cash, so Total is max(a,b)
                    let cIsRound = (c % 10000 === 0);
                    if (cIsRound) {
                        possibleTotals.push({ total: Math.max(a, b), confidence: 1 });
                    } else {
                        possibleTotals.push({ total: c, confidence: 1 });
                    }
                }
            }
        }
    }
    return possibleTotals;
}

console.log(findMathTotal([100, 2000, 22900, 23000, 25000])); // Alfamart
console.log(findMathTotal([25000, 75000, 100000])); // Kopi Kenangan
