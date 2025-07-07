

import React, { useState } from 'react';
import postService from '../Services/PostService';
import './CreatePostForm.css';

function CreatePostForm({ onPostCreated }) {
    const [text, setText] = useState('');
    const [imageFile, setImageFile] = useState(null);
    const [error, setError] = useState('');
    const [isLoading, setIsLoading] = useState(false);

    const handleFileChange = (event) => {
        if (event.target.files && event.target.files[0]) {
            setImageFile(event.target.files[0]);
        } else {
            setImageFile(null);
        }
    };

    const handleSubmit = async (event) => {
        event.preventDefault();
        setError('');
        if (!text.trim() && !imageFile) {
            setError('Post must contain text or an image.');
            return;
        }
        setIsLoading(true);

        const formData = new FormData();


        const postJsonBlob = new Blob([JSON.stringify({ text: text })], { type: "application/json" });
        formData.append("postData", postJsonBlob);


        if (imageFile) {
            formData.append("imageFile", imageFile);
        }

        try {
            const response = await postService.createPost(formData);
            if (onPostCreated) {
                onPostCreated(response.data);
            }
            setText('');
            setImageFile(null);
            event.target.reset();
        } catch (err) {
            console.error('Failed to create post:', err);
            setError(err.response?.data?.message || 'Failed to create post.');
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="post-form">
            <h3>Create a new Post</h3>
            <form onSubmit={handleSubmit}>
        <textarea
            value={text}
            onChange={(e) => setText(e.target.value)}
            placeholder="What's on your mind?"
            rows="4"
            maxLength="280"
        />

                <div style={{ margin: '1rem 0' }}>
                    <label htmlFor="imageUpload" style={{ cursor: 'pointer', padding: '0.5rem', border: '1px dashed #ccc' }}>
                        {imageFile ? `Selected: ${imageFile.name}` : 'Add a photo'}
                    </label>
                    <input
                        id="imageUpload"
                        type="file"
                        accept="image/png, image/jpeg, image/gif"
                        onChange={handleFileChange}
                        style={{ display: 'none' }}
                    />
                </div>

                <div className="form-actions">
                    {error && <p className="error-message">{error}</p>}
                    <button type="submit" disabled={isLoading || (!text.trim() && !imageFile)}>
                        {isLoading ? 'Posting...' : 'Post'}
                    </button>
                </div>
            </form>
        </div>
    );
}

export default CreatePostForm;