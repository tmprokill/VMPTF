# Variant 16
# Level 1: 4. Напишіть функцію, яка приймає три параметри (a, b, c) і виводить на екран найменше з них.
def print_min_of_three(a, b, c) -> None:
    smallest = min(a, b, c)
    print(f'Smallest number: {smallest}')


# Level 2: 4. Напишіть функцію, яка приймає рядок та повертає його обернений варіант. 
# Наприклад, "hello" повинно повернути "olleh".
def reverse_string(text: str) -> str:
    return text[::-1]

# Level 3: 4. Реалізуйте програму, яка визначає, чи є слово паліндромом (читається однаково з обох боків).
def is_palindrome(text: str) -> bool:
    clean_text = text.replace(' ', '')
    return clean_text == clean_text[::-1]

# Level 4: 4.	Розробіть алгоритм сортування масиву чисел методом швидкого сортування (QuickSort) 
# та виведіть відсортований масив.
def quick_sort_and_print(arr: list[int]):
    n = len(arr)
    quick_sort(arr, 0, n - 1)
    
    for val in arr:
        print(val, end=" ")

def quick_sort(arr: list[int], low: int, high: int):
    if low >= high:
        return
    
    pivot_ix = high

    i = low - 1
    j = low
    while j < pivot_ix:
        if arr[j] < arr[pivot_ix]:
            i += 1
            arr[i],arr[j] = arr[j],arr[i]
        j += 1
    
    new_pivot_ix = i + 1
    arr[new_pivot_ix],arr[pivot_ix] = arr[pivot_ix],arr[new_pivot_ix]
    
    quick_sort(arr, low, new_pivot_ix - 1)
    quick_sort(arr, new_pivot_ix + 1, high)


if __name__ == "__main__":
    print_min_of_three(1, -4, 20)

    text_to_reverse = 'hello'
    print(f'text to reverse: {text_to_reverse} | reversed text: {reverse_string(text_to_reverse)}')

    not_palindrome = 're23s'
    palindrome = 'reger'
    print(f'is palindrome {not_palindrome}: {is_palindrome(not_palindrome)}')
    print(f'is palindrome {palindrome}: {is_palindrome(palindrome)}')

    arr = [10, 5, 4, 2, 1, -2]
    quick_sort_and_print(arr)