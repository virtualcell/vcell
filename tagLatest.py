import fire


def tagLatest(current, next):
    curent_arr = current.split(".")
    next_arr = next.split(".")

    for index, part in enumerate(next_arr):
        if(index < len(curent_arr)):
            if part < curent_arr[index]:
                return False
    if(len(current) > len(next)):
        return False

    return True


if __name__ == '__main__':
    fire.Fire(tagLatest)