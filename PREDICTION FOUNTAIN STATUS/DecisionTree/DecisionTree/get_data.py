from firebase_admin import storage


def get_time(element):
    return element.time_created


def load_imgs(img_path):
    bucket = storage.bucket()
    # blob = bucket.blob('images')
    blob = bucket.blob('')
    blob_lst = list(bucket.list_blobs())
    if len(blob_lst) == 0:
        return 0, None
    blob_lst.sort(key=get_time)
    file_names = []

    for i in blob_lst:
        f_name = img_path + '/' + str(i.time_created) + '.jpg'
        file_names.append(f_name)
        with open(f_name, 'wb') as file:
            i.download_to_file(file)
            i.delete()
    return 1, file_names
